import React, {createRef, Fragment, useState} from 'react'
import './Login.scss'
import {MiddleContent} from "../middle_content/MiddleContent";
import {Button, Form, Row} from "react-bootstrap";
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
                <Form>
                    <Form.Group>
                        <Form.Label>Username</Form.Label>
                        <Form.Control ref={usernameRef} type="text"/>
                    </Form.Group>

                    <Form.Group className="pt-2">
                        <Form.Label>Password</Form.Label>
                        <Form.Control ref={passwordRef} type="password"/>
                    </Form.Group>

                    <Form.Group className={"pt-3"}>
                        <Button onClick={() => login()}>Login</Button>
                    </Form.Group>
                </Form>

                <Fragment>
                    {error &&
                        <p className="error pt-2">Invalid credentials!</p>}
                </Fragment>
            </MiddleContent>
        </Row>
    )
}
