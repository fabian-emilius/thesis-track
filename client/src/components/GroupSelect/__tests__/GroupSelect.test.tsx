import { render, screen, fireEvent } from '@testing-library/react';
import { GroupSelect } from '../GroupSelect';
import { GroupProvider } from '../../../providers/GroupProvider';

const mockGroups = [
  { id: '1', name: 'Software Engineering', description: 'SE group' },
  { id: '2', name: 'Data Science', description: 'DS group' },
];

jest.mock('../../../requests/request', () => ({
  request: jest.fn().mockResolvedValue({ groups: mockGroups, total: 2 }),
}));

describe('GroupSelect', () => {
  it('renders group select dropdown', () => {
    render(
      <GroupProvider>
        <GroupSelect />
      </GroupProvider>
    );
    expect(screen.getByRole('combobox')).toBeInTheDocument();
  });

  it('calls onGroupSelect when a group is selected', () => {
    const onGroupSelect = jest.fn();
    render(
      <GroupProvider>
        <GroupSelect onGroupSelect={onGroupSelect} />
      </GroupProvider>
    );

    fireEvent.click(screen.getByRole('combobox'));
    fireEvent.click(screen.getByText('Software Engineering'));

    expect(onGroupSelect).toHaveBeenCalledWith('1');
  });

  it('displays loading state when groups are being fetched', () => {
    render(
      <GroupProvider>
        <GroupSelect />
      </GroupProvider>
    );

    expect(screen.getByRole('combobox')).toBeDisabled();
  });
});
