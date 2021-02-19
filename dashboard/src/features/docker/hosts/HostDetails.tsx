import React, { useEffect, useState } from 'react'
import { RouteComponentProps } from 'react-router-dom'
import {
    CRow, CCol, CCard, CCardHeader, CCardBody
} from '@coreui/react'
import CIcon from '@coreui/icons-react'
import styled from 'styled-components'
import { useSelector } from 'react-redux'

import { RootState } from '../../../app/rootReducer'
import ApiLink from '../../reusable/ApiLink'
import FetchSpinner from '../../reusable/FetchSpinner'
import { Host } from './types'

const StyledIcon = styled(CIcon)`
    margin-right: 10px;
`

type TParams = { name: string }

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

    const details = loading ? <FetchSpinner /> : <div>{host?.details?.id}</div>

    return (
        <>
            <CRow>
                <CCol xs='12' sm='6' md='4'>
                    <CCard>
                        <CCardHeader color="info" textColor="white">
                            <StyledIcon name="cib-docker" size="xl" />{name}
                            <ApiLink color="white" uri={"/api/docker/" + name} />
                        </CCardHeader>
                        <CCardBody>
                            {details}
                        </CCardBody>
                    </CCard>
                </CCol>
            </CRow>
        </>
    )
}

export default HostDetails
