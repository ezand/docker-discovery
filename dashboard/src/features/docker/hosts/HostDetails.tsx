import React, { useEffect, useState } from 'react'
import { RouteComponentProps } from 'react-router-dom'
import {
    CRow, CCol, CCard, CCardHeader, CCardBody, CCardFooter, CWidgetBrand, CBadge, CTooltip, CWidgetIcon
} from '@coreui/react'
import CIcon from '@coreui/icons-react'
import styled from 'styled-components'
import { useSelector } from 'react-redux'

import { RootState } from '../../../app/rootReducer'
import ApiLink from '../../reusable/ApiLink'
import FetchSpinner from '../../reusable/FetchSpinner'
import DockerHostBadge from '../../reusable/DockerHostBadge'
import { Host } from './types'
import DockerContainerIcon from '../../../assets/icons/docker-container.svg'

const StyledIcon = styled(CIcon)`
    margin-right: 10px;
`

const NameSpan = styled.span`
    text-transform: capitalize;
`

const BadgeContainer = styled.ul`
    list-style-type: none;
    margin: 0;
    padding: 0;
    overflow: hidden;
    li {
        float: left;
        padding-right: 5px;
    }
`

type TParams = { name: string }

interface DetailsProps {
    host?: Host
}

interface ThumbProps {
    value?: boolean,
    tooltipText?: string
}

const Thumb = ({ value, tooltipText = value ? "Yes" : "No" }: ThumbProps) => (
    <CTooltip content={tooltipText}>
        <CIcon style={{ color: value ? "#2eb85c" : "#f9b115" }} name={value ? "cil-thumb-up" : "cil-thumb-down"} />
    </CTooltip>
)

const Details = ({ host }: DetailsProps) => {
    if (host) {
        const items = [
            { name: "Id", value: host.details?.id },
            { name: "Name", value: host.name },
            { name: "Version", value: host.details?.version },
            { name: "API version", value: host.details?.apiVersion },
            { name: "IPv4 forwarding", value: <Thumb value={host.details?.iPv4forwarding} /> },
            { name: "Experimental build", value: <Thumb value={host.details?.experimentalBuild} /> },
            { name: "Ping", value: <Thumb value={host.details?.ping === "OK"} tooltipText={host.details?.ping === "OK" ? "Ping OK" : "Ping failed"} /> },
            { name: "Live restore", value: <Thumb value={host.details?.liveRestoreEnabled} /> },
            { name: "Swap limit", value: <Thumb value={host.details?.swapLimit} /> },
        ]

        return (
            <>
                {items.map(item => (
                    <CRow key={item.name}>
                        <CCol md="3">
                            <small>{item.name}</small>
                        </CCol>
                        <CCol xs="12" md="9">
                            {typeof item.value === "string" ? <small>{item.value}</small> : item.value}
                        </CCol>
                    </CRow>
                ))}
            </>
        )
    } else return null
}

const HostDetails = ({ match }: RouteComponentProps<TParams>) => {
    const { hosts } = useSelector((state: RootState) => state.hostList)
    const [host, setHost] = useState<Host | undefined>()
    const [loading, setLoading] = useState(false)
    const name = match.params.name

    useEffect(() => {
        const host = hosts.find(host => host.name === name)
        if (host) {
            setHost(host)
            setLoading(host.loadingDetails ? true : false)
        }
    }, [name, hosts])

    const details = loading ? <FetchSpinner /> : <Details host={host} />

    const sum = (...vals: any[]) => vals.filter(val => val !== undefined).reduce((a, b) => a + b, 0)

    return (
        <>
            <CRow>
                <CCol xs='12' sm='6' md='6' lg='6'>
                    <CCard>
                        <CCardHeader color="info" textColor="white">
                            <StyledIcon name="cib-docker" size="xl" />
                            <NameSpan>{name}</NameSpan>
                            <ApiLink color="white" uri={"/api/docker/" + name} />
                        </CCardHeader>
                        <CCardBody>
                            {details}
                        </CCardBody>
                        <CCardFooter>
                            <div className="card-header-actions">
                                <BadgeContainer>
                                    <li>
                                        <CTooltip content="Architechture">
                                            <CBadge color='primary'>{host?.details?.architecture}</CBadge>
                                        </CTooltip>
                                    </li>
                                    <li>
                                        <DockerHostBadge local={host?.local} />
                                    </li>
                                </BadgeContainer>
                            </div>
                        </CCardFooter>
                    </CCard>
                </CCol>

                <CCol xs='12' sm='3' md='3' lg='3'>
                    <CTooltip content="Containers">
                        <CWidgetBrand
                            color="warning"
                            rightHeader={host?.details?.containersRunning.toString()}
                            rightFooter="running"
                            leftHeader={sum(host?.details?.containersStopped, host?.details?.containersPaused).toString()}
                            leftFooter="stopped">
                            <img height={52} style={{ margin: "15px" }} src={DockerContainerIcon} alt="Docker Container Icon" />
                        </CWidgetBrand>
                    </CTooltip>
                </CCol>

                <CCol xs='12' sm='3' md='3' lg='3'>
                    <CTooltip content="Containers">
                        <CWidgetBrand
                            colSpan={2}
                            color="warning"
                            rightHeader={host?.details?.containersRunning.toString()}
                            rightFooter="running">
                            <img height={52} style={{ margin: "15px" }} src={DockerContainerIcon} alt="Docker Container Icon" />
                        </CWidgetBrand>
                    </CTooltip>
                </CCol>
            </CRow>

            <CRow>
                <CCol xs="12" sm="3" md="3" lg="3">
                    <CWidgetIcon text="Images" header={host?.details?.images.toString()} color="primary" iconPadding={false}>
                        <CIcon width={24} name="cil-image-1" />
                    </CWidgetIcon>
                </CCol>

                <CCol xs="12" sm="3" md="3" lg="3">
                    <CWidgetIcon text="Containers" header={host?.details?.containers.toString()} color="primary" iconPadding={false}>
                        <CIcon width={24} name="cil-square" />
                    </CWidgetIcon>
                </CCol>

                <CCol xs="12" sm="3" md="3" lg="3">
                    <CWidgetIcon text="Labels" header={host?.details?.labels.length.toString()} color="primary" iconPadding={false}>
                        <CIcon width={24} name="cil-notes" />
                    </CWidgetIcon>
                </CCol>

                <CCol xs="12" sm="3" md="3" lg="3">
                    <CWidgetIcon text="Event listeners" header={host?.details?.nEventsListener.toString()} color="primary" iconPadding={false}>
                        <CIcon width={24} name="cil-bullhorn" />
                    </CWidgetIcon>
                </CCol>
            </CRow>
        </>
    )
}

export default HostDetails
