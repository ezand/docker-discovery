import { Host } from '../docker/hosts/types'
import { badgeText, badgeColor } from '../reusable/DockerHostBadge'

const dockerHosts = (hosts: Host[]) => {
    const hostItems = hosts.map(host => ({
        _tag: 'CSidebarNavItem',
        name: host.name,
        to: '/docker/hosts/' + host.name,
        icon: 'cib-docker',
        badge: {
            color: badgeColor(host.local),
            text: badgeText(host.local)
        }
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
