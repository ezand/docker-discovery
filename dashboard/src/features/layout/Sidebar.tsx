import React from 'react'
import {
    CSidebar,
    CSidebarBrand,
    CSidebarNav,
    CCreateElement,
    CSidebarNavDivider,
    CSidebarNavItem,
    CSidebarNavDropdown,
    CSidebarNavTitle,
} from '@coreui/react'
import CIcon from '@coreui/icons-react'
import styled from 'styled-components'
import { useSelector } from 'react-redux'

import { RootState } from '../../app/rootReducer'
import navigation from '../sidebar/nav'

const Title = styled.a`
  margin-left: 10px;
`

const Sidebar = () => {
    const { hosts } = useSelector((state: RootState) => state.hostList)
    const show = true // use redux state

    return (
        <CSidebar show={show}>
            <CSidebarBrand className="d-md-down-none">
                <CIcon className="c-sidebar-brand-full" name="cib-docker" height={35} />
                <Title className="c-sidebar-brand-full">Docker Discovery</Title>
            </CSidebarBrand>

            <CSidebarNav>
                <CCreateElement
                    items={navigation(hosts)}
                    components={{
                        CSidebarNavDivider,
                        CSidebarNavDropdown,
                        CSidebarNavItem,
                        CSidebarNavTitle
                    }}
                />
            </CSidebarNav>
        </CSidebar>
    )
}

export default Sidebar;
