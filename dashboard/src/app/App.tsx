import React, { useEffect } from 'react';
import { HashRouter, Route, Switch } from 'react-router-dom';
import { useDispatch } from 'react-redux'

import '../styles/style.scss'

import { fetchDockerHosts } from '../features/docker/hosts/hostsSlice'

const loading = (
  <div className="pt-3 text-center">
    <div className="sk-spinner sk-spinner-pulse"></div>
  </div>
)

const Layout = React.lazy(() => import('../features/layout/Layout'))

const App = () => {
  const dispatch = useDispatch()

  useEffect(() => {
    dispatch(fetchDockerHosts())
  }, [dispatch]);

  return (
    <HashRouter>
      <React.Suspense fallback={loading}>
        <Switch>
          <Route path="/" render={() => <Layout />} />
        </Switch>
      </React.Suspense>
    </HashRouter>
  )
}

export default App;
