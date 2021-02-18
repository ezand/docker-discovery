import React from 'react'
import { useSelector, useDispatch } from 'react-redux'
import { CHeader, CSubheader, CToggler, CBreadcrumbRouter, CHeaderNav, CHeaderNavItem, CHeaderNavLink, CLink } from '@coreui/react'
import CIcon from '@coreui/icons-react'

const Header = () => {
    const dispatch = useDispatch()
    const sidebarShow = true // TODO useSelector(state => state.sidebarShow)

    const toggleSidebar = () => {
        const val = [true, 'responsive'].includes(sidebarShow) ? false : 'responsive'
        //dispatch({ type: 'set', sidebarShow: val })
    }

    const toggleSidebarMobile = () => {
        const val = [false, 'responsive'].includes(sidebarShow) ? true : 'responsive'
        //dispatch({ type: 'set', sidebarShow: val })
    }

    return (
        <CHeader withSubheader>
            <CToggler
                inHeader
                className="ml-md-3 d-lg-none"
                onClick={toggleSidebarMobile} />
            <CToggler
                inHeader
                className="ml-3 d-md-down-none"
                onClick={toggleSidebar} />

            <CHeaderNav className="d-md-down-none mr-auto">
                <CHeaderNavItem className="px-3" >
                    <CHeaderNavLink>Dashboard</CHeaderNavLink>
                </CHeaderNavItem>
                <CHeaderNavItem className="px-3">
                    <CHeaderNavLink>Users</CHeaderNavLink>
                </CHeaderNavItem>
                <CHeaderNavItem className="px-3">
                    <CHeaderNavLink>Settings</CHeaderNavLink>
                </CHeaderNavItem>
            </CHeaderNav>

            <CHeaderNav className="px-3">
                Profile, notifications etc. here
            </CHeaderNav>

            <CSubheader className="px-3 justify-content-between">
                <CBreadcrumbRouter className="border-0 c-subheader-nav m-0 px-0 px-md-3" />

                <div className="d-md-down-none mfe-2 c-subheader-nav">
                    <CLink className="c-subheader-nav-link" aria-current="page">
                        <CIcon name="cil-graph" alt="Dashboard" />&nbsp;Dashboard
                    </CLink>
                    <CLink className="c-subheader-nav-link" href="#">
                        <CIcon name="cil-settings" alt="Settings" />&nbsp;Settings
                    </CLink>
                </div>
            </CSubheader>
        </CHeader>
    )
}

export default Header
