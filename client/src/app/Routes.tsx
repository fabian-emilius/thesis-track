import React, { lazy, Suspense } from 'react'
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import AuthenticatedArea from './layout/AuthenticatedArea/AuthenticatedArea'
import PageLoader from '../components/PageLoader'
import { LazyExoticComponent } from 'react'
import { useGroupContext } from '../providers/GroupContext/hooks'

const GroupsPage = lazy(() => import('../pages/GroupsPage/GroupsPage')) as LazyExoticComponent<
  () => JSX.Element
>
const GroupManagementPage = lazy(
  () => import('../pages/GroupManagementPage/GroupManagementPage'),
) as LazyExoticComponent<() => JSX.Element>
const GroupSettingsPage = lazy(
  () => import('../pages/GroupSettingsPage/GroupSettingsPage'),
) as LazyExoticComponent<() => JSX.Element>
const NotFoundPage = lazy(
  () => import('../pages/NotFoundPage/NotFoundPage'),
) as LazyExoticComponent<() => JSX.Element>
const PrivacyPage = lazy(() => import('../pages/PrivacyPage/PrivacyPage')) as LazyExoticComponent<
  () => JSX.Element
>
const ImprintPage = lazy(() => import('../pages/ImprintPage/ImprintPage')) as LazyExoticComponent<
  () => JSX.Element
>
const AboutPage = lazy(() => import('../pages/AboutPage/AboutPage')) as LazyExoticComponent<
  () => JSX.Element
>
const ThesisOverviewPage = lazy(
  () => import('../pages/ThesisOverviewPage/ThesisOverviewPage'),
) as LazyExoticComponent<() => JSX.Element>
const PresentationOverviewPage = lazy(
  () => import('../pages/PresentationOverviewPage/PresentationOverviewPage'),
) as LazyExoticComponent<() => JSX.Element>
const BrowseThesesPage = lazy(
  () => import('../pages/BrowseThesesPage/BrowseThesesPage'),
) as LazyExoticComponent<() => JSX.Element>
const DashboardPage = lazy(
  () => import('../pages/DashboardPage/DashboardPage'),
) as LazyExoticComponent<() => JSX.Element>
const LogoutPage = lazy(() => import('../pages/LogoutPage/LogoutPage')) as LazyExoticComponent<
  () => JSX.Element
>
const SettingsPage = lazy(
  () => import('../pages/SettingsPage/SettingsPage'),
) as LazyExoticComponent<() => JSX.Element>
const ReplaceApplicationPage = lazy(
  () => import('../pages/ReplaceApplicationPage/ReplaceApplicationPage'),
) as LazyExoticComponent<() => JSX.Element>
const ManageTopicsPage = lazy(
  () => import('../pages/ManageTopicsPage/ManageTopicsPage'),
) as LazyExoticComponent<() => JSX.Element>
const TopicPage = lazy(() => import('../pages/TopicPage/TopicPage')) as LazyExoticComponent<
  () => JSX.Element
>
const PresentationPage = lazy(
  () => import('../pages/PresentationPage/PresentationPage'),
) as LazyExoticComponent<() => JSX.Element>
const ReviewApplicationPage = lazy(
  () => import('../pages/ReviewApplicationPage/ReviewApplicationPage'),
) as LazyExoticComponent<() => JSX.Element>
const ThesisPage = lazy(() => import('../pages/ThesisPage/ThesisPage')) as LazyExoticComponent<
  () => JSX.Element
>
const LandingPage = lazy(() => import('../pages/LandingPage/LandingPage')) as LazyExoticComponent<
  () => JSX.Element
>

const AppRoutes = () => {
  const { currentGroup } = useGroupContext()
  return (
    <Suspense fallback={<PageLoader />}>
      <BrowserRouter>
        <Routes>
          <Route
            path='/management/thesis-applications/:applicationId?'
            element={<Navigate to={`/groups/${currentGroup?.slug}/applications`} replace />}
          />
          <Route path='/applications/thesis' element={<Navigate to='/' replace />} />
          <Route
            path='/topics'
            element={<Navigate to={`/groups/${currentGroup?.slug}/topics`} replace />}
          />
          <Route
            path='/theses'
            element={<Navigate to={`/groups/${currentGroup?.slug}/theses`} replace />}
          />
          <Route
            path='/applications'
            element={<Navigate to={`/groups/${currentGroup?.slug}/applications`} replace />}
          />
          <Route
            path='/dashboard'
            element={
              <AuthenticatedArea>
                <DashboardPage />
              </AuthenticatedArea>
            }
          />
          <Route
            path='/settings/:tab?'
            element={
              <AuthenticatedArea>
                <SettingsPage />
              </AuthenticatedArea>
            }
          />
          <Route
            path='/groups/:groupId/submit-application/:topicId?'
            element={
              <AuthenticatedArea>
                <ReplaceApplicationPage />
              </AuthenticatedArea>
            }
          />
          <Route
            path='/groups/:groupId/edit-application/:applicationId'
            element={
              <AuthenticatedArea>
                <ReplaceApplicationPage />
              </AuthenticatedArea>
            }
          />
          <Route
            path='/presentations'
            element={
              <AuthenticatedArea requireAuthentication={false}>
                <PresentationOverviewPage />
              </AuthenticatedArea>
            }
          />
          <Route
            path='/presentations/:presentationId'
            element={
              <AuthenticatedArea size='md' requireAuthentication={false}>
                <PresentationPage />
              </AuthenticatedArea>
            }
          />
          <Route
            path='/groups/:groupId/topics'
            element={
              <AuthenticatedArea requiredGroups={['admin', 'advisor', 'supervisor', 'group_admin']}>
                <ManageTopicsPage />
              </AuthenticatedArea>
            }
          />
          <Route
            path='/groups/:groupId/topics/:topicId'
            element={
              <AuthenticatedArea size='md' requireAuthentication={false}>
                <TopicPage />
              </AuthenticatedArea>
            }
          />
          <Route
            path='/groups/:groupId/applications/:applicationId?'
            element={
              <AuthenticatedArea
                collapseNavigation={true}
                requiredGroups={['admin', 'advisor', 'supervisor', 'group_admin']}
              >
                <ReviewApplicationPage />
              </AuthenticatedArea>
            }
          />
          <Route
            path='/groups/:groupId/theses'
            element={
              <AuthenticatedArea>
                <BrowseThesesPage />
              </AuthenticatedArea>
            }
          />
          <Route
            path='/groups/:groupId/theses/:thesisId'
            element={
              <AuthenticatedArea>
                <ThesisPage />
              </AuthenticatedArea>
            }
          />
          <Route
            path='/overview'
            element={
              <AuthenticatedArea>
                <ThesisOverviewPage />
              </AuthenticatedArea>
            }
          />
          <Route path='/about' element={<AboutPage />} />
          <Route path='/imprint' element={<ImprintPage />} />
          <Route path='/privacy' element={<PrivacyPage />} />
          <Route path='/logout' element={<LogoutPage />} />
          <Route
            path='/groups'
            element={
              <AuthenticatedArea requiredGroups={['admin']}>
                <GroupsPage />
              </AuthenticatedArea>
            }
          />
          <Route
            path='/groups/:groupId/manage'
            element={
              <AuthenticatedArea requiredGroups={['admin', 'group_admin']}>
                <GroupManagementPage />
              </AuthenticatedArea>
            }
          />
          <Route
            path='/groups/:groupId/settings'
            element={
              <AuthenticatedArea requiredGroups={['admin', 'group_admin']}>
                <GroupSettingsPage />
              </AuthenticatedArea>
            }
          />
          <Route path='/' element={<LandingPage />} />
          <Route path='*' element={<NotFoundPage />} />
        </Routes>
      </BrowserRouter>
    </Suspense>
  )
}

export default AppRoutes
