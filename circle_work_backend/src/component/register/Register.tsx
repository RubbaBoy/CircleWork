import React, {createRef, Fragment, useEffect, useState} from 'react'
import './Register.scss'
import {MiddleContent} from "../middle_content/MiddleContent";
import {Button, Modal, ModalBody, ModalHeader, ModalTitle, Row} from "react-bootstrap";
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
                <Row>
                    <input ref={usernameRef} name="username" type="text"/>
                </Row>

                <Row className={"pt-2"}>
                    <input ref={passwordRef} name="password" type="password"/>
                </Row>

                <Row className={"pt-2"}>
                    <input ref={passwordRef} name="password" type="password" placeholder="Circle Code (optional)"/>
                </Row>

                <Row className={"pt-3"}>
                    <Button onClick={() => register(false)}>Create Circle</Button>
                </Row>

                <Row className={"pt-3"}>
                    <Button onClick={() => register(true)}>Join Circle</Button>
                </Row>

                <Fragment>
                    {error &&
                        <p className="error">Invalid credentials!</p>}
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
