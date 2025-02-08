import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { Group, GroupDetails } from '../../types/group';
import { GetGroupsResponse, GetGroupDetailsResponse } from '../../requests/responses/group';
import { request } from '../../requests/request';

type GroupError = {
  message: string;
  code?: string;
  details?: unknown;
};

/**
 * Context type definition for group management
 * @interface GroupContextType
 */
interface GroupContextType {
  /** Currently selected group details */
  currentGroup: GroupDetails | null;
  /** Function to set the current group */
  setCurrentGroup: (group: GroupDetails | null) => void;
  /** List of all groups */
  groups: Group[];
  /** Loading state indicator */
  loading: boolean;
  /** Error state */
  error: GroupError | null;
  /** Fetch all groups */
  fetchGroups: () => Promise<void>;
  /** Fetch details for a specific group */
  fetchGroupDetails: (groupId: string) => Promise<void>;
  /** Total number of groups */
  totalGroups: number;
}

const GroupContext = createContext<GroupContextType | undefined>(undefined);

/**
 * Provider component for group management functionality
 * @param {Object} props - Component props
 * @param {React.ReactNode} props.children - Child components
 */
export function GroupProvider({ children }: { children: React.ReactNode }) {
  const [currentGroup, setCurrentGroup] = useState<GroupDetails | null>(null);
  const [groups, setGroups] = useState<Group[]>([]);
  const [totalGroups, setTotalGroups] = useState<number>(0);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<GroupError | null>(null);

  const fetchGroups = useCallback(async (): Promise<void> => {
    try {
      setLoading(true);
      setError(null);
      const response = await request<GetGroupsResponse, never>('GET', '/api/groups');
      setGroups(response.groups);
      setTotalGroups(response.total);
    } catch (err) {
      setError({
        message: 'Failed to fetch groups',
        details: err instanceof Error ? err.message : String(err)
      });
      console.error('Error fetching groups:', err);
    } finally {
      setLoading(false);
    }
  }, []);

  const fetchGroupDetails = useCallback(async (groupId: string): Promise<void> => {
    try {
      setLoading(true);
      setError(null);
      const response = await request<GetGroupDetailsResponse, never>('GET', `/api/groups/${groupId}`);
      setCurrentGroup(response);
    } catch (err) {
      setError({
        message: 'Failed to fetch group details',
        code: 'GROUP_DETAILS_ERROR',
        details: err instanceof Error ? err.message : String(err)
      });
      console.error('Error fetching group details:', err);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchGroups();
  }, [fetchGroups]);

  return (
    <GroupContext.Provider
      value={{
        currentGroup,
        setCurrentGroup,
        groups,
        loading,
        error,
        fetchGroups,
        fetchGroupDetails,
        totalGroups,
      }}
    >
      {children}
    </GroupContext.Provider>
  );
}

/**
 * Custom hook to access group context
 * @throws {Error} When used outside of GroupProvider
 * @returns {GroupContextType} Group context value
 */
export function useGroup(): GroupContextType {
  const context = useContext(GroupContext);
  if (context === undefined) {
    throw new Error('useGroup must be used within a GroupProvider');
  }
  return context;
}
