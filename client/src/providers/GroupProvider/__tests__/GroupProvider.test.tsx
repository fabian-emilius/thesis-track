import { render, act, waitFor } from '@testing-library/react';
import { GroupProvider, useGroup } from '../GroupProvider';

const mockGroups = [
  { id: '1', name: 'Software Engineering', description: 'SE group' },
  { id: '2', name: 'Data Science', description: 'DS group' },
];

jest.mock('../../../requests/request', () => ({
  request: jest.fn().mockResolvedValue({ groups: mockGroups, total: 2 }),
}));

describe('GroupProvider', () => {
  const TestComponent = () => {
    const { groups, loading, error } = useGroup();
    return (
      <div>
        {loading && <div>Loading...</div>}
        {error && <div>Error: {error}</div>}
        {groups.map((group) => (
          <div key={group.id}>{group.name}</div>
        ))}
      </div>
    );
  };

  it('provides groups data to children', async () => {
    const { getByText } = render(
      <GroupProvider>
        <TestComponent />
      </GroupProvider>
    );

    expect(getByText('Loading...')).toBeInTheDocument();

    await waitFor(() => {
      expect(getByText('Software Engineering')).toBeInTheDocument();
      expect(getByText('Data Science')).toBeInTheDocument();
    });
  });

  it('handles fetch error', async () => {
    jest.spyOn(console, 'error').mockImplementation(() => {});
    jest.mock('../../../requests/request', () => ({
      request: jest.fn().mockRejectedValue(new Error('Failed to fetch')),
    }));

    const { getByText } = render(
      <GroupProvider>
        <TestComponent />
      </GroupProvider>
    );

    await waitFor(() => {
      expect(getByText('Error: Failed to fetch groups')).toBeInTheDocument();
    });
  });

  it('updates current group when setCurrentGroup is called', async () => {
    const TestComponent = () => {
      const { currentGroup, setCurrentGroup } = useGroup();
      return (
        <div>
          <button
            onClick={() =>
              setCurrentGroup({
                id: '1',
                name: 'Test Group',
                description: 'Test Description',
                topicCount: 0,
                thesisCount: 0,
                members: [],
                createdAt: '',
                updatedAt: '',
              })
            }
          >
            Set Group
          </button>
          {currentGroup && <div>{currentGroup.name}</div>}
        </div>
      );
    };

    const { getByText } = render(
      <GroupProvider>
        <TestComponent />
      </GroupProvider>
    );

    act(() => {
      getByText('Set Group').click();
    });

    await waitFor(() => {
      expect(getByText('Test Group')).toBeInTheDocument();
    });
  });
});
