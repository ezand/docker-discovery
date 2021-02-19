import React from 'react'
import { CModal, CModalBody, CModalHeader, CModalFooter, CButton } from '@coreui/react'
import styled from 'styled-components'

import JsonView from './JsonView'
import FetchSpinner from './FetchSpinner'

interface JsonViewDialogProps {
    show: boolean
    onClose: Function
    json: any
    loading: boolean
    title?: string
}

const StyledBody = styled(CModalBody)`
    height: 300px;
    width: 100%;
    overflow: scroll;
`

const JsonViewDialog = ({show, loading, title, onClose, json}: JsonViewDialogProps) => {
    return (
        <CModal size="lg" show={show} onClose={onClose}>
            <CModalHeader closeButton>{title}</CModalHeader>
            <StyledBody>
                {loading ? <FetchSpinner /> : <JsonView json={json} />}
            </StyledBody>
            <CModalFooter>
                <CButton color="primary" onClick={onClose}>Close</CButton>
            </CModalFooter>
        </CModal>
    )
}

export default JsonViewDialog
