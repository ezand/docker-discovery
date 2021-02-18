import React from 'react';

import '../styles/style.scss'

import Sidebar from '../features/layout/Sidebar'

function App() {
  return (
    <div className="App">
      <div className="c-app c-default-layout">
        <Sidebar />
        <div className="c-wrapper">
          Content here!
        </div>
      </div>
    </div>
  );
}

export default App;
