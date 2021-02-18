import { Host } from "../docker/hosts/types"

const localHostBadge = ({
    color: 'info',
    text: 'local',
})

const remoteHostBadge = ({
    color: 'warning',
    text: 'remote',
})

const nav = (hosts: Host[]) => {
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

export default nav
