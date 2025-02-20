import React, { lazy, Suspense } from 'react'
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import AuthenticatedArea from './layout/AuthenticatedArea/AuthenticatedArea'
import PageLoader from '../components/PageLoader/PageLoader'
import { GroupProtectedRoute } from '../components/GroupProtectedRoute/GroupProtectedRoute'
import { PublicArea } from './layout/PublicArea/PublicArea'

const GroupsOverviewPage = lazy(() => import('../pages/GroupsOverviewPage/GroupsOverviewPage'))
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

const AppRoutes = () => {
  return (
    <Suspense fallback={<PageLoader />}>
      <BrowserRouter>
        <Routes>
          <Route
            path='/groups'
            element={
              <AuthenticatedArea>
                <GroupsOverviewPage />
              </AuthenticatedArea>
            }
          />
          <Route
            path='/groups/:groupSlug/*'
            element={
              <PublicArea>
                <Routes>
                <Route path='dashboard' element={
                  <GroupProtectedRoute>
                    <DashboardPage />
                  </GroupProtectedRoute>
                } />
                <Route path='theses' element={
                  <GroupProtectedRoute>
                    <BrowseThesesPage />
                  </GroupProtectedRoute>
                } />
                <Route path='theses/:thesisId' element={
                  <GroupProtectedRoute>
                    <ThesisPage />
                  </GroupProtectedRoute>
                } />
                <Route path='overview' element={
                  <GroupProtectedRoute>
                    <ThesisOverviewPage />
                  </GroupProtectedRoute>
                } />
                <Route path='topics' element={
                  <GroupProtectedRoute requiredRole='advisor' requireAuthentication={true}>
                    <ManageTopicsPage />
                  </GroupProtectedRoute>
                } />
                <Route path='topics/:topicId' element={
                  <GroupProtectedRoute requireAuthentication={false}>
                    <TopicPage />
                  </GroupProtectedRoute>
                } />
                <Route path='presentations' element={
                  <GroupProtectedRoute requireAuthentication={false}>
                    <PresentationOverviewPage />
                  </GroupProtectedRoute>
                } />
                <Route path='presentations/:presentationId' element={
                  <GroupProtectedRoute requireAuthentication={false}>
                    <PresentationPage />
                  </GroupProtectedRoute>
                } />
                <Route path='applications/:applicationId?' element={
                  <GroupProtectedRoute requiredRole='advisor' requireAuthentication={true}>
                    <ReviewApplicationPage />
                  </GroupProtectedRoute>
                } />
                <Route path='submit-application/:topicId?' element={
                  <GroupProtectedRoute>
                    <ReplaceApplicationPage />
                  </GroupProtectedRoute>
                } />
                <Route path='edit-application/:applicationId' element={
                  <GroupProtectedRoute>
                    <ReplaceApplicationPage />
                  </GroupProtectedRoute>
                } />
                <Route path='' element={<Navigate to='dashboard' replace />} />
              </Routes>
              </PublicArea>
            }
          />
          <Route
            path='/management/thesis-applications/:applicationId?'
            element={<Navigate to='/applications' replace />}
          />
          <Route path='/applications/thesis' element={<Navigate to='/' replace />} />
          <Route
            path='/settings/:tab?'
            element={
              <AuthenticatedArea>
                <SettingsPage />
              </AuthenticatedArea>
            }
          />
          <Route path='/about' element={<PublicArea><AboutPage /></PublicArea>} />
          <Route path='/imprint' element={<PublicArea><ImprintPage /></PublicArea>} />
          <Route path='/privacy' element={<PublicArea><PrivacyPage /></PublicArea>} />
          <Route path='/logout' element={<PublicArea><LogoutPage /></PublicArea>} />
          <Route path='/' element={<Navigate to='/groups' replace />} />
          <Route path='*' element={<NotFoundPage />} />
        </Routes>
      </BrowserRouter>
    </Suspense>
  )
}

export default AppRoutes
