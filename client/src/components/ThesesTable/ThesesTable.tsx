import React from 'react';
import { DataTable } from 'mantine-datatable';
import { useThesesContext } from '../../providers/ThesesProvider/hooks';
import { useGroupFilter } from '../../hooks/utility';
import { Text } from '@mantine/core';
import { useCurrentGroup } from '../../providers/GroupContext/hooks';

/**
 * Component for displaying theses in a table format with group-based filtering
 * Automatically filters theses based on the current group context
 */
const ThesesTable: React.FC = () => {
  const { theses, loading } = useThesesContext();
  const currentGroup = useCurrentGroup();
  const groupTheses = useGroupFilter(theses?.content);

  if (!currentGroup) {
    return (
      <Text c="dimmed">
        Please select a group to view theses.
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
      records={groupTheses || []}
      fetching={loading}
      columns={[
        { accessor: 'title', title: 'Title' },
        { accessor: 'type', title: 'Type' },
        { accessor: 'state', title: 'State' },
        { accessor: 'student.name', title: 'Student' },
        { accessor: 'advisor.name', title: 'Advisor' },
      ]}
      onRowClick={(record: { id: string }) => {
        window.location.href = `/theses/${record.id}`;
      }}
    />
  );
};

export default ThesesTable;
