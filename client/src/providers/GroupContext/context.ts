import { createContext } from 'react'
import { IGroupContextValue } from './types'

export const GroupContext = createContext<IGroupContextValue | undefined>(undefined)
