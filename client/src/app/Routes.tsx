import React, { lazy, Suspense } from 'react'
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import AuthenticatedArea from './layout/AuthenticatedArea/AuthenticatedArea'
import PageLoader from '../components/PageLoader/PageLoader'

const NotFoundPage = lazy(() => import('../pages/NotFoundPage/NotFoundPage'))
const PrivacyPage = lazy(() => import('../pages/PrivacyPage/PrivacyPage'))
const ImprintPage = lazy(() => import('../pages/ImprintPage/ImprintPage'))
const AboutPage = lazy(() => import('../pages/AboutPage/AboutPage'))
const ThesisOverviewPage = lazy(() => import('../pages/ThesisOverviewPage/ThesisOverviewPage'))
const PresentationOverviewPage = lazy(
  () => import('../pages/PresentationOverviewPage/PresentationOverviewPage'),
)
const BrowseThesesPage = lazy(() => import('../pages/BrowseThesesPage/BrowseThesesPage'))
const DashboardPage = lazy(() => import('../pages/DashboardPage/DashboardPage'))
const LogoutPage = lazy(() => import('../pages/LogoutPage/LogoutPage'))
const SettingsPage = lazy(() => import('../pages/SettingsPage/SettingsPage'))
const ReplaceApplicationPage = lazy(
  () => import('../pages/ReplaceApplicationPage/ReplaceApplicationPage'),
)
const ManageTopicsPage = lazy(() => import('../pages/ManageTopicsPage/ManageTopicsPage'))
const TopicPage = lazy(() => import('../pages/TopicPage/TopicPage'))
const PresentationPage = lazy(() => import('../pages/PresentationPage/PresentationPage'))
const ReviewApplicationPage = lazy(
  () => import('../pages/ReviewApplicationPage/ReviewApplicationPage'),
)
const ThesisPage = lazy(() => import('../pages/ThesisPage/ThesisPage'))
const LandingPage = lazy(() => import('../pages/LandingPage/LandingPage'))

// New group-related page imports
const GroupOverviewPage = lazy(() => import('../pages/GroupOverviewPage'))
const GroupLandingPage = lazy(() => import('../pages/GroupLandingPage'))
const GroupSettingsPage = lazy(() => import('../pages/GroupSettingsPage'))
const AdminGroupPage = lazy(() => import('../pages/AdminGroupPage'))

const AppRoutes = () => {
  return (
    <Suspense fallback={<PageLoader />}>
      <BrowserRouter>
        <Routes>
          <Route
            path='/management/thesis-applications/:applicationId?'
            element={<Navigate to='/applications' replace />}
          />
          <Route path='/applications/thesis' element={<Navigate to='/' replace />} />
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
            path='/submit-application/:topicId?'
            element={
              <AuthenticatedArea>
                <ReplaceApplicationPage />
              </AuthenticatedArea>
            }
          />
          <Route
            path='/edit-application/:applicationId'
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
            path='/topics'
            element={
              <AuthenticatedArea requiredGroups={['admin', 'advisor', 'supervisor']}>
                <ManageTopicsPage />
              </AuthenticatedArea>
            }
          />
          <Route
            path='/topics/:topicId'
            element={
              <AuthenticatedArea size='md' requireAuthentication={false}>
                <TopicPage />
              </AuthenticatedArea>
            }
          />
          <Route
            path='/applications/:applicationId?'
            element={
              <AuthenticatedArea
                collapseNavigation={true}
                requiredGroups={['admin', 'advisor', 'supervisor']}
              >
                <ReviewApplicationPage />
              </AuthenticatedArea>
            }
          />
          <Route
            path='/theses'
            element={
              <AuthenticatedArea>
                <BrowseThesesPage />
              </AuthenticatedArea>
            }
          />
          <Route
            path='/theses/:thesisId'
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

          {/* New group-related routes */}
          <Route
            path='/groups'
            element={
              <AuthenticatedArea>
                <GroupOverviewPage />
              </AuthenticatedArea>
            }
          />
          <Route
            path='/groups/:groupSlug'
            element={
              <AuthenticatedArea>
                <GroupLandingPage />
              </AuthenticatedArea>
            }
          />
          <Route
            path='/groups/:groupSlug/settings'
            element={
              <AuthenticatedArea>
                <GroupSettingsPage />
              </AuthenticatedArea>
            }
          />
          <Route
            path='/admin/groups/new'
            element={
              <AuthenticatedArea requiredGroups={['admin']}>
                <AdminGroupPage />
              </AuthenticatedArea>
            }
          />

          <Route path='/about' element={<AboutPage />} />
          <Route path='/imprint' element={<ImprintPage />} />
          <Route path='/privacy' element={<PrivacyPage />} />
          <Route path='/logout' element={<LogoutPage />} />
          <Route path='/' element={<LandingPage />} />
          <Route path='*' element={<NotFoundPage />} />
        </Routes>
      </BrowserRouter>
    </Suspense>
  )
}

export default AppRoutes
