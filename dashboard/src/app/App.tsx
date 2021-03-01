import React, { useEffect } from 'react';
import { HashRouter, Route, Switch } from 'react-router-dom';
import { useDispatch } from 'react-redux'

import '../styles/style.scss'

import { fetchDockerHosts } from '../features/docker/hosts/hostsSlice'
import FetchSpinner from '../features/reusable/FetchSpinner'

const Layout = React.lazy(() => import('../features/layout/Layout'))

const App = () => {
  const dispatch = useDispatch()

  useEffect(() => {
    dispatch(fetchDockerHosts())
  }, [dispatch])

  return (
    <HashRouter>
      <React.Suspense fallback={<FetchSpinner />}>
        <Switch>
          <Route path="/" render={() => <Layout />} />
        </Switch>
      </React.Suspense>
    </HashRouter>
  )
}

export default App;
