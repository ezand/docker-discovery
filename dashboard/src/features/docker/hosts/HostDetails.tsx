import React from 'react'
import { RouteComponentProps } from 'react-router-dom'
import {
    CRow, CCol, CCard, CCardHeader, CCardBody, CCardFooter
} from '@coreui/react'

import ApiLink from '../../reusable/ApiLink'

type TParams = { name: string }

const HostDetails = ({match}: RouteComponentProps<TParams>) => {
    const name = match.params.name

    return (
        <>
            <CRow>
                <CCol xs='12' sm='6' md='4'>
                    <CCardHeader>
                        {name}
                        <ApiLink uri={"/api/docker/" + name} />
                    </CCardHeader>
                </CCol>
            </CRow>
        </>
    )
}

export default HostDetails
