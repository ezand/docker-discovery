import React, { Suspense } from 'react'
import { CContainer, CFade } from '@coreui/react'

const loading = (
    <div className="pt-3 text-center">
        <div className="sk-spinner sk-spinner-pulse"></div>
    </div>
)

const Content = () => {
    return (
        <main className="c-main">
            <CContainer fluid>
                <Suspense fallback={loading}>
                    Main Content
                </Suspense>
            </CContainer>
        </main>
    )
}

export default Content
