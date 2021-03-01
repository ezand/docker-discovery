import React from 'react'
import ReactJson from 'react-json-view'

interface JsonViewProps {
    json: any
}
const JsonView = ({json}: JsonViewProps) => {
    return (
        <ReactJson name={null} src={json} />
    )
}

export default JsonView
