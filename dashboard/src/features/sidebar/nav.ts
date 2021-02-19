import { Host } from '../docker/hosts/types'

const dockerHosts = (hosts: Host[]) => {
    const localHostBadge = ({
        color: 'info',
        text: 'local',
    })
    
    const remoteHostBadge = ({
        color: 'warning',
        text: 'remote',
    })

    const hostItems = hosts.map(host => ({
        _tag: 'CSidebarNavItem',
        name: host.name,
        to: '/docker/hosts/' + host.name,
        icon: 'cib-docker',
        badge: host.local ? localHostBadge : remoteHostBadge
    }))

    return [{
        _tag: 'CSidebarNavTitle',
        _children: ['Docker Hosts']
    }, ...hostItems]
}

const nav = (hosts: Host[]) => {
    return [...dockerHosts(hosts)]
}

export default nav
