import React from 'react';

import '../styles/style.scss'

import Header from '../features/layout/Header'
import Sidebar from '../features/layout/Sidebar'
import Content from '../features/layout/Content'
import Footer from '../features/layout/Footer'

function App() {
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
  );
}

export default App;
