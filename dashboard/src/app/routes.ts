import React from 'react'

const Dashboard = React.lazy(() => import('../features/dashboard/Dashboard'))
const HostDetails = React.lazy(() => import('../features/docker/hosts/HostDetails'))

interface RouteDefinition {
    path: string;
    exact?: boolean;
    component?: any;
}

const routes: RouteDefinition[] = [
    { path: '/', exact: true },
    { path: '/dashboard', exact: true, component: Dashboard },
    { path: '/docker/hosts/:name', exact: true, component: HostDetails },
]

export default routes
