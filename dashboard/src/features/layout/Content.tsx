import React, { Suspense } from 'react'
import { Redirect, Route, Switch } from 'react-router-dom'
import { CContainer, CFade } from '@coreui/react'

import routes from '../../app/routes'
import FetchSpinner from '../../features/reusable/FetchSpinner'

const Content = () => {
    
    return (
        <main className="c-main">
            <CContainer fluid>
                <Suspense fallback={<FetchSpinner />}>
                    <Switch>
                        {routes.map((route, idx) => {
                            return route.component && (
                                <Route
                                    key={idx}
                                    path={route.path}
                                    exact={route.exact}
                                    render={props => (
                                        <CFade>
                                            <route.component {...props} />
                                        </CFade>
                                    )} />
                            )
                        })}
                        <Redirect from="/" to="/dashboard" />
                    </Switch>
                </Suspense>
            </CContainer>
        </main>
    )
}

export default Content
