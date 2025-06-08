import React from 'react';
import ReactDOM from 'react-dom/client';
import AppRouter from './App';
import './index.css';

async function loadConfig() {
  const res = await fetch('/config.json');
  const config = await res.json();
  window.API_URL = config.API_URL;
}

loadConfig().then(() => {
  const root = ReactDOM.createRoot(document.getElementById('root'));
  root.render(
    <React.StrictMode>
      <AppRouter />
    </React.StrictMode>
  );
});