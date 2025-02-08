import { render, screen, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { GroupOverviewPage } from '../GroupOverviewPage';
import { GroupProvider } from '../../../providers/GroupProvider';

const mockGroups = [
  {
    id: '1',
    name: 'Software Engineering',
    description: 'SE research group',
    topicCount: 5,
    thesisCount: 10,
  },
  {
    id: '2',
    name: 'Data Science',
    description: 'DS research group',
    topicCount: 3,
    thesisCount: 7,
  },
];

jest.mock('../../../requests/request', () => ({
  request: jest.fn().mockResolvedValue({ groups: mockGroups, total: 2 }),
}));

describe('GroupOverviewPage', () => {
  const renderComponent = () =>
    render(
      <BrowserRouter>
        <GroupProvider>
          <GroupOverviewPage />
        </GroupProvider>
      </BrowserRouter>
    );

  it('renders group cards with correct information', async () => {
    renderComponent();

    await waitFor(() => {
      expect(screen.getByText('Software Engineering')).toBeInTheDocument();
      expect(screen.getByText('Data Science')).toBeInTheDocument();
    });

    expect(screen.getByText('5 active topics · 10 theses')).toBeInTheDocument();
    expect(screen.getByText('3 active topics · 7 theses')).toBeInTheDocument();
  });

  it('displays loading state initially', () => {
    renderComponent();
    expect(screen.getByTestId('page-loader')).toBeInTheDocument();
  });

  it('handles error state', async () => {
    jest.spyOn(console, 'error').mockImplementation(() => {});
    jest.mock('../../../requests/request', () => ({
      request: jest.fn().mockRejectedValue(new Error('Failed to fetch')),
    }));

    renderComponent();

    await waitFor(() => {
      expect(screen.getByText('Failed to fetch groups')).toBeInTheDocument();
    });
  });
});
