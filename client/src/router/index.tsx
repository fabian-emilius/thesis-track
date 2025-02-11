import React, { Suspense } from 'react';
import { Routes, Route } from 'react-router';
import PageLoader from '../components/PageLoader/PageLoader';
import GroupOverview from '../components/GroupOverview';
import GroupPage from '../pages/GroupPage';

const AppRouter = () => {
  return (
    <Suspense fallback={<PageLoader />}>
      <Routes>
        <Route path="/groups" element={<GroupOverview />} />
        <Route path="/groups/:groupId" element={<GroupPage />} />
      </Routes>
    </Suspense>
  );
};

export default AppRouter;
