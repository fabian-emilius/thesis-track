import React from 'react';
import { DataTable } from 'mantine-datatable';
import { useTopicsContext } from '../../providers/TopicsProvider/hooks';
import { useGroupFilter } from '../../hooks/utility';
import { Text } from '@mantine/core';
import { useCurrentGroup } from '../../providers/GroupContext/hooks';

/**
 * Component for displaying topics in a table format with group-based filtering
 * Automatically filters topics based on the current group context
 */
const TopicsTable: React.FC = () => {
  const { topics, loading } = useTopicsContext();
  const currentGroup = useCurrentGroup();
  const groupTopics = useGroupFilter(topics?.content);

  if (!currentGroup) {
    return (
      <Text c="dimmed">
        Please select a group to view topics.
      </Text>
    );
  }

  return (
    <DataTable
      withBorder
      borderRadius="sm"
      withColumnBorders
      striped
      highlightOnHover
      records={groupTopics || []}
      fetching={loading}
      columns={[
        { accessor: 'title', title: 'Title' },
        { accessor: 'type', title: 'Type' },
        { accessor: 'supervisor.name', title: 'Supervisor' },
        { accessor: 'status', title: 'Status' },
      ]}
      onRowClick={(record: { id: string }) => {
        window.location.href = `/topics/${record.id}`;
      }}
    />
  );
};

export default TopicsTable;
