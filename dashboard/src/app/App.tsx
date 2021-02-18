import React from 'react';
import { HashRouter, Route, Switch } from 'react-router-dom';

import '../styles/style.scss'

const loading = (
  <div className="pt-3 text-center">
    <div className="sk-spinner sk-spinner-pulse"></div>
  </div>
)

const Layout = React.lazy(() => import('../features/layout/Layout'))

const App = () => {
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
