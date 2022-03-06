import {useState} from "react";

export default function useToken() {
    const getToken = () => {
        return sessionStorage.getItem('token');
    };

    const [token, setToken] = useState<string | null>(getToken());

    const saveToken = (userToken: string | undefined) => {
        if (userToken == undefined) {
            sessionStorage.clear()
            setToken(null)
        } else {
            sessionStorage.setItem('token', userToken);
            setToken(userToken);
        }
    };

    return {
        setToken: saveToken,
        token,
        loggedIn: () => !!token,
        logout: () => saveToken(undefined)
    }
}
