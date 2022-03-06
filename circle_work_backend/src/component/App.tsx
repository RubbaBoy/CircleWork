import React, {Fragment, ReactElement} from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import {Home} from "./home/Home";
import {Navigate, Route, Routes} from 'react-router';
import {Login} from "./login/Login";
import useToken from "../logic/useToken";
import {Register} from "./register/Register";
import {Dashboard} from "./dashboard/Dashboard";
import {Goals} from "./goals/Goals";
import {Resources} from "./resources/Resources";
import {Categories} from "./categories/Categories";

function App() {

    const { token, setToken, loggedIn, logout } = useToken();

    function guarded(element: ReactElement): ReactElement {
        if (!token) {
            return <Login setToken={setToken}/>
        }

        return element
    }

    return (
        <Fragment>
            <Routes>
                <Route path="/" element={<Home loggedIn={loggedIn} logout={logout}/>}>
                    <Route index element={<Home loggedIn={loggedIn} logout={logout}/>}/>
                </Route>
                <Route path="login" element={guarded(<Navigate to="/"/>)}/>
                <Route path="register" element={<Register setToken={setToken}/>}/>
                <Route path="dashboard" element={guarded(<Dashboard/>)}/>
                <Route path="goals" element={guarded(<Goals/>)}/>
                <Route path="resources" element={guarded(<Categories/>)}/>
                <Route path="resources/:id" element={guarded(<Resources/>)}/>
            </Routes>
        </Fragment>
    );
}

export default App;
