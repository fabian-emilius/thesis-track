import React, { PropsWithChildren, useEffect, useMemo, useState } from 'react'
import {
  AuthenticationContext,
  IAuthenticationContext,
  IDecodedAccessToken,
  IDecodedRefreshToken,
} from './context'
import Keycloak from 'keycloak-js'
import { GLOBAL_CONFIG } from '../../config/global'
import { jwtDecode } from 'jwt-decode'
import { getAuthenticationTokens, useAuthenticationTokens } from '../../hooks/authentication'
import { useSignal } from '../../hooks/utility'
import { IUser } from '../../requests/responses/user'
import { doRequest } from '../../requests/request'

interface IAuthenticationProviderProps {}

const keycloak = new Keycloak({
  realm: GLOBAL_CONFIG.keycloak.realm,
  url: GLOBAL_CONFIG.keycloak.host,
  clientId: GLOBAL_CONFIG.keycloak.client_id,
})

const AuthenticationProvider = (props: PropsWithChildren<IAuthenticationProviderProps>) => {
  const { children } = props

  const [universityId, setUniversityId] = useState<string>();
  const [user, setUser] = useState<IUser>()
  const [authenticationTokens, setAuthenticationTokens] = useAuthenticationTokens()
  const {
    signal: readySignal,
    triggerSignal: triggerReadySignal,
    ref: { isTriggerred: isReady },
  } = useSignal()

  useEffect(() => {
    setUser(undefined)

    let refreshTokenTimeout: ReturnType<typeof setTimeout> | undefined = undefined

    const refreshAccessToken = () => {
      keycloak
        .updateToken(60 * 5)
        .then((isSuccess) => {
          if (!isSuccess) {
            setAuthenticationTokens(undefined)
          }
        })
    }

    const storeTokens = () => {
      const refreshToken = keycloak.refreshToken
      const accessToken = keycloak.token

      const decodedAccessToken = accessToken
        ? jwtDecode<IDecodedAccessToken>(accessToken)
        : undefined
      const decodedRefreshToken = refreshToken
        ? jwtDecode<IDecodedRefreshToken>(refreshToken)
        : undefined

      console.log('decoded keycloak tokens', decodedAccessToken, decodedRefreshToken)

      if (decodedRefreshToken?.exp) {
        console.log(
          'refresh token expires in seconds',
          Math.floor(decodedRefreshToken?.exp - Date.now() / 1000),
        )
      }

      // refresh if already expired
      if (decodedRefreshToken?.exp && decodedRefreshToken.exp <= Date.now() / 1000) {
        return setAuthenticationTokens(undefined)
      } else if (decodedAccessToken?.exp && decodedAccessToken.exp <= Date.now() / 1000) {
        return refreshAccessToken()
      }

      if (decodedRefreshToken?.exp) {
        refreshTokenTimeout = setTimeout(
          () => {
            setAuthenticationTokens(undefined)
          },
          Math.min(Math.max(decodedRefreshToken.exp * 1000 - Date.now(), 0), 3600 * 24 * 1000),
        )
      }

      if (accessToken && refreshToken) {
        setAuthenticationTokens({
          access_token: accessToken,
          refresh_token: refreshToken,
        })
      } else {
        setAuthenticationTokens(undefined)
      }
    }

    const storedTokens = getAuthenticationTokens()

    keycloak.onTokenExpired = () => refreshAccessToken()
    keycloak.onAuthRefreshSuccess = () => storeTokens()

    console.log('Initializing keycloak...')

    void keycloak
      .init({
        refreshToken: storedTokens?.refresh_token,
        token: storedTokens?.access_token,
      })
      .then(() => {
        console.log('Keycloak initialized')

        storeTokens()
        triggerReadySignal()
      })
      .catch((error) => {
        console.log('Keycloak init error', error)
      })

    return () => {
      if (refreshTokenTimeout) {
        clearTimeout(refreshTokenTimeout)
      }

      keycloak.onAuthRefreshSuccess = undefined
      keycloak.onTokenExpired = undefined
    }
  }, [])

  useEffect(() => {
    if (!isReady) {
      return
    }

    if (authenticationTokens?.access_token) {
      const decodedAccessToken = jwtDecode<IDecodedAccessToken>(authenticationTokens.access_token)

      setUniversityId(decodedAccessToken[GLOBAL_CONFIG.keycloak.university_id_jwt_attribute] || undefined)
    } else {
      setUniversityId(undefined)
    }
  }, [authenticationTokens?.access_token, isReady])

  useEffect(() => {
    setUser(undefined)

    if (isReady && universityId) {
      return doRequest<IUser>('/v1/user-info', {
        method: 'POST',
        requiresAuth: true
      }, (err, res) => {
        if (res?.ok) {
          setUser(res.data)
        }

        if (err) {
          console.error(err)
        }
      })
    }
  }, [universityId, isReady])

  const contextValue = useMemo<IAuthenticationContext>(() => {
    return {
      isAuthenticated: !!authenticationTokens?.access_token,
      user: authenticationTokens?.access_token ? user : undefined,
      groups: [],
      login: () =>
        readySignal.then(() => {
          !keycloak.authenticated && keycloak.login()
        }),
      logout: (redirectUri: string) =>
        readySignal.then(() => {
          setAuthenticationTokens(undefined)

          keycloak.authenticated &&
            keycloak.logout({
              redirectUri: `${window.location.origin}${redirectUri}`,
            })
        }),
    }
  }, [user, !!authenticationTokens?.access_token, location.origin])

  return (
    <AuthenticationContext.Provider value={contextValue}>{children}</AuthenticationContext.Provider>
  )
}

export default AuthenticationProvider
