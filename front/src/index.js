import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import {Provider} from "react-redux";
import pointStore from "./PointStore";
import {BrowserRouter} from "react-router-dom";

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <Provider store={pointStore}>
        <BrowserRouter>
            <App/>
        </BrowserRouter>
    </Provider>
);

