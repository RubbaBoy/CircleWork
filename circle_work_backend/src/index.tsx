import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from "./component/App";
import {BrowserRouter} from "react-router-dom";
import 'react-date-range/dist/styles.css'; // main style file
import 'react-date-range/dist/theme/default.css'; // theme css file
import './scss/theme.scss'

ReactDOM.render(
  <React.StrictMode>
      <BrowserRouter>
          <App />
      </BrowserRouter>
  </React.StrictMode>,
  document.getElementById('root')
);
