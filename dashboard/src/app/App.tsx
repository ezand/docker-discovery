import React from 'react';
import { useDispatch } from 'react-redux'

import logo from './logo.svg';
import './App.css';
import HostList from '../features/docker/hosts/HostList'
import { addHost, removeHost, fetchDockerHosts } from '../features/docker/hosts/hostsSlice'

function App() {
  const dispatch = useDispatch()

  const addId = () => {
    dispatch(addHost({id: Math.random() + ''}))
  }

  const removeIt = () => {
    dispatch(removeHost("0.26016769421395947"))
  }

  const fetchIt = () => {
    dispatch(fetchDockerHosts())
  }

  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          Edit <code>src/App.tsx</code> and save to reload.
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
        <HostList />
        <button onClick={addId}>Add host!</button>
        <button onClick={removeIt}>Remove host!</button>
        <button onClick={fetchIt}>Fetch!</button>
      </header>
    </div>
  );
}

export default App;