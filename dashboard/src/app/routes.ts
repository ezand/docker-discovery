import React from 'react'

const Dashboard = React.lazy(() => import('../features/dashboard/Dashboard'))
const HostDetails = React.lazy(() => import('../features/docker/hosts/HostDetails'))

interface RouteDefinition {
    path: string;
    exact?: boolean;
    component?: any;
    name: string;
}

const routes: RouteDefinition[] = [
    { path: '/', exact: true, name: 'Home' },
    { path: '/dashboard', exact: true, name: 'Dashboard', component: Dashboard },
    { path: '/docker/hosts/:name', exact: true, name: 'Host', component: HostDetails },
]

export default routes
