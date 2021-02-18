import React from 'react'
import { CFooter } from '@coreui/react'

const Footer = () => {
    return (
        <CFooter fixed={false}>
            <div>
                <a href="https://github.com/ezand/docker-discovery" target="_blank" rel="noopener noreferrer">Docker Discovery</a>
                <span className="ml-1">&copy; 2021 Eirik Stenersen Sand</span>
            </div>
            <div className="mfs-auto">
                <span className="mr-1">Powered by</span>
                <a href="https://coreui.io/react" target="_blank" rel="noopener noreferrer">CoreUI</a>
            </div>
        </CFooter>
    )
}

export default Footer
