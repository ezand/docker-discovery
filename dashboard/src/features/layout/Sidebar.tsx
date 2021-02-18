import React from 'react'
import { CSidebar, CSidebarBrand } from '@coreui/react'
import CIcon from '@coreui/icons-react'
import styled from 'styled-components'

const Title = styled.a`
  margin-left: 10px;
`

const Sidebar = () => {
    const show = true // use redux state

    return (
        <CSidebar show={show}>
            <CSidebarBrand className="d-md-down-none">
                <CIcon className="c-sidebar-brand-full" name="cib-docker" height={35} />
                <Title className="c-sidebar-brand-full">Docker Discovery</Title>
            </CSidebarBrand>
        </CSidebar>
    )
}

export default Sidebar;
