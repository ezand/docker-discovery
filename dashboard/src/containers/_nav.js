import React from 'react'
import CIcon from '@coreui/icons-react'

const _nav =  [
  {
    _tag: 'CSidebarNavItem',
    name: 'Dashboard',
    to: '/dashboard',
    icon: <CIcon name="cil-speedometer" customClasses="c-sidebar-nav-icon"/>,
  },
  {
    _tag: 'CSidebarNavTitle',
    _children: ['Docker Hosts']
  },
  {
    _tag: 'CSidebarNavItem',
    name: 'Sand Server',
    to: '/theme/colors',
    icon: 'cib-docker',
    badge: {
      color: 'info',
      text: 'local',
    }
  },
  {
    _tag: 'CSidebarNavItem',
    name: 'Daenerys',
    to: '/theme/colors',
    icon: 'cib-docker',
    badge: {
      color: 'warning',
      text: 'remote',
    }
  },
  {
    _tag: 'CSidebarNavTitle',
    _children: ['Events']
  },
  {
    _tag: 'CSidebarNavItem',
    name: 'Docker',
    to: '/theme/colors',
    icon: 'cib-docker',
  },
  {
    _tag: 'CSidebarNavItem',
    name: 'WebSockets',
    to: '/theme/colors',
    icon: 'cil-globe-alt',
  },
  {
    _tag: 'CSidebarNavDropdown',
    name: 'MQTT',
    route: '/mqtt',
    icon: 'cil-library',
    _children: [
      {
        _tag: 'CSidebarNavItem',
        name: 'Home Assistant',
        to: '/mqtt/homeassistant',
      },
    ]
  },
  {
    _tag: 'CSidebarNavTitle',
    _children: ['Messages']
  },
  {
    _tag: 'CSidebarNavItem',
    name: 'Information',
    to: '',
    icon: {
      name: 'cil-star',
      className: 'text-info'
    },
    label: true
  },
  {
    _tag: 'CSidebarNavItem',
    name: 'Warnings',
    to: '',
    icon: {
      name: 'cil-star',
      className: 'text-warning'
    },
    label: true
  },
  {
    _tag: 'CSidebarNavItem',
    name: 'Errors',
    to: '',
    icon: {
      name: 'cil-star',
      className: 'text-danger'
    },
    label: true
  },
  {
    _tag: 'CSidebarNavDivider',
    className: 'm-2'
  }
]

export default _nav
