interface RouteDefinition {
    path: string;
    exact?: boolean;
    name: string;
    component?: any;
}

const routes: RouteDefinition[] = [
    { path: '/', exact: true, name: 'Home' }
]

export default routes
