import React, {createRef, Fragment, useState} from 'react'
import './Login.scss'
import {MiddleContent} from "../middle_content/MiddleContent";
import {Button, Row} from "react-bootstrap";
import {fetchApi} from "../../logic/request-helper";

interface LoginProps {
    setToken: (token: string) => void
}

export const Login = (props: LoginProps) => {
    let usernameRef = createRef<HTMLInputElement>()
    let passwordRef = createRef<HTMLInputElement>()
    const [error, setError] = useState<boolean>(false)

    function login() {
        fetchApi('auth/login', {
            method: 'POST',
            body: JSON.stringify({
                'username': usernameRef.current?.value ?? '',
                'password': passwordRef.current?.value ?? ''
            })
        }).then(async res => {
            let json = await res.json()

            if (res.status != 200) {
                console.log('Bad request status ' + res.status)
                console.log(json)
                setError(true)
                return;
            }

            setError(false)

            props.setToken(json['token'] ?? '')
            sessionStorage.setItem('circle_id', json['circle_id'])
        })
    }

    return (
        <Row className="Login d-flex justify-content-center">
            <MiddleContent>
                <Row>
                    <input ref={usernameRef} name="username" type="text"/>
                </Row>

                <Row className={"pt-2"}>
                    <input ref={passwordRef} name="password" type="password"/>
                </Row>

                <Row className={"pt-3"}>
                    <Button onClick={() => login()}>Login</Button>
                </Row>

                <Fragment>
                    {error &&
                        <p className="error">Invalid credentials!</p>}
                </Fragment>
            </MiddleContent>
        </Row>
    )
}
