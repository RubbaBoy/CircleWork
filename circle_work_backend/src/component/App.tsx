import React, {Fragment, ReactElement, useEffect, useState} from 'react';
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
import CategoriesContext, {listCategories} from "../logic/Category";
import {GoalCategory} from "../logic/objects";

function App() {

    const { token, setToken, loggedIn, logout } = useToken();
    const [categories, setCategories] = useState<GoalCategory[]>([])
    const [ready, setReady] = useState<boolean>(false)

    function guarded(element: ReactElement, ensureReady: boolean = true): ReactElement {
        if (!token) {
            return <Login setToken={setToken}/>
        }

        if (ensureReady && !ready) {
            return <p>Loading...</p>
        }

        return element
    }

    useEffect(() => {
        listCategories()
            .then(setCategories)
            .finally(() => setReady(true))
    }, [])

    return (
        <CategoriesContext.Provider value={categories}>
            <Routes>
                <Route path="/" element={<Home loggedIn={loggedIn} logout={logout}/>}>
                    <Route index element={<Home loggedIn={loggedIn} logout={logout}/>}/>
                </Route>
                <Route path="login" element={guarded(<Navigate to="/"/>, false)}/>
                <Route path="register" element={<Register setToken={setToken}/>}/>
                <Route path="dashboard" element={guarded(<Dashboard/>)}/>
                <Route path="goals" element={guarded(<Goals/>)}/>
                <Route path="resources" element={guarded(<Categories/>)}/>
                <Route path="resources/:id" element={guarded(<Resources/>)}/>
            </Routes>
        </CategoriesContext.Provider>
    );
}

export default App;
