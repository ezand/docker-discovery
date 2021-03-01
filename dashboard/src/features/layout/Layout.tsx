import React from 'react';

import Header from './Header'
import Sidebar from './Sidebar'
import Content from './Content'
import Footer from './Footer'

const Layout = () => {
    return (
        <div className="c-app c-default-layout">
            <Sidebar />
            <div className="c-wrapper">
                <Header />
                <div className="c-body">
                    <Content />
                </div>
                <Footer />
            </div>
        </div>
    )
}

export default Layout
