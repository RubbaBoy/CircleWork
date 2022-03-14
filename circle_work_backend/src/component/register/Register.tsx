import React, {createRef, Fragment, useEffect, useState} from 'react'
import './Register.scss'
import {MiddleContent} from "../middle_content/MiddleContent";
import {Button, Form, Modal, ModalBody, ModalHeader, ModalTitle, Row} from "react-bootstrap";
import {fetchApi, fetchAuthed} from "../../logic/request-helper";
import DropIn from "braintree-web-drop-in-react";
import {Dropin} from "braintree-web-drop-in";

interface RegisterProps {
    setToken: (token: string) => void
}

export const Register = (props: RegisterProps) => {
    let instance: Dropin;

    let usernameRef = createRef<HTMLInputElement>()
    let passwordRef = createRef<HTMLInputElement>()
    let codeRef = createRef<HTMLInputElement>()
    const [error, setError] = useState<boolean>(false)
    const [showModal, setShowModal] = useState(false);
    const [clientToken, setClientToken] = useState<string>('');

    useEffect(() => {
        fetchAuthed("payment/client_token")
            .then(async res => setClientToken((await res.json())['token']))
    })

    function register(useCode: boolean) {
        fetchApi('auth/register', {
            method: 'POST',
            // mode: 'cors',
            body: JSON.stringify({
                'username': usernameRef.current?.value ?? '',
                'password': passwordRef.current?.value ?? '',
                'circle_id': useCode ? codeRef.current?.value ?? -1 : -1
            })
        }).then(async res => {
            let json = await res.json()

            if (res.status != 200) {
                console.log('Bad request status ' + res.status)
                console.log(json)
                setError(true)
                return;
            }

            console.log(json);

            setError(false)

            props.setToken(json['token'] ?? '')
            sessionStorage.setItem('circle_id', json['circle_id'])

            setShowModal(true)
        })
    }

    async function buy() {
        // Send the nonce to your server
        const {nonce} = await instance.requestPaymentMethod();
        await fetchApi('payment/pay', {body: JSON.stringify({
                'nonce': nonce,
            })}).then(async res => {
            let json = await res.json()

            if (res.status != 200) {
                console.log('Bad payment request status ' + res.status)
                console.log(json)
                setError(true)
                return;
            }

            console.log('Payment response:');
            console.log(json);
        })
    }

    return (
        <Row className="Login d-flex justify-content-center">
            <MiddleContent>
                <Form>
                    <Form.Group>
                        <Form.Label>Username</Form.Label>
                        <Form.Control ref={usernameRef} name="username" type="text"/>
                    </Form.Group>

                    <Form.Group className="pt-2">
                        <Form.Label>Password</Form.Label>
                        <Form.Control ref={passwordRef} name="password" type="password"/>
                    </Form.Group>

                    <Form.Group className="pt-2">
                        <Form.Label>Circle Code</Form.Label>
                        <Form.Control ref={codeRef} name="code" type="text" placeholder="Circle Code (optional)"/>
                    </Form.Group>

                    <Form.Group className={"pt-3"}>
                        <Button onClick={() => register(false)}>Create Circle</Button>
                        <Button className="mx-2" onClick={() => register(true)}>Join Circle</Button>
                    </Form.Group>
                </Form>

                <Fragment>
                    {error &&
                        <p className="error pt-2">Invalid credentials!</p>}
                </Fragment>
            </MiddleContent>


            <Modal show={showModal}>
                <ModalHeader>
                    <ModalTitle>Motivation Payment</ModalTitle>
                </ModalHeader>
                <ModalBody>
                    <div>
                        <DropIn
                            options={{authorization: clientToken}}
                            onInstance={(dropin) => (instance = dropin)}
                        />
                        <Button onClick={buy.bind(this)}>Buy</Button>
                    </div>
                </ModalBody>
            </Modal>
        </Row>
    )
}
