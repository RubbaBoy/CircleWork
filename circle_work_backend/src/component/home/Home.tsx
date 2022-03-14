import React, {Fragment, useEffect, useState} from 'react'
import './Home.scss'
import {Card, Col, Row} from "react-bootstrap";
import {Link} from "react-router-dom";
import {Charity} from "../../logic/objects";
import {fetchApi} from "../../logic/request-helper";


export function navSideButton(name: string, path: string, color: string = '425dff'): JSX.Element {
    return (
        <Link className="side-button" to={path} style={{backgroundColor: `#${color}`}}>{name}</Link>
    )
}

export function hrefSideButton(name: string, anchor: string, color: string = '425dff'): JSX.Element {
    return (
        <a className="side-button" href={anchor} style={{backgroundColor: `#${color}`}}>{name}</a>
    )
}

interface HomeProps {
    loggedIn: () => boolean
    logout: () => void
}

export const Home = (props: HomeProps) => {
    const {loggedIn, logout} = props
    const [charities, setCharities] = useState<Charity[]>([]);

    useEffect(() => {
        console.log('useEffect');
        fetchApi('charities/list', {method: 'GET'}).then(async res => {
            let json = await res.json();

            if (res.status != 200) {
                console.log('Bad request status ' + res.status)
                console.log(json)
                return
            }
            let charities: Charity[] = json
            console.log(charities);
            setCharities(charities);
        })
    }, [])

    return (
        <Row className="Home justify-content-md-center m-0">
            <div className="side-buttons">
                {hrefSideButton('About', '#about')}
                {hrefSideButton('Donation Leaderboard', '#donation')}
            </div>

            <Col xs={2} className="left-col">
                <div className="spacer"></div>

                <div className="sidebar-incomplete">
                    <div className="logo-parent"></div>

                    <div className="link-container">
                        {!loggedIn() && <Fragment>
                            <Link to={"login"}>Login</Link>

                            <Link to={"register"}>Register</Link>
                        </Fragment>}

                        {loggedIn() && <Fragment>
                            <a href="/" onClick={() => logout()}>Log out</a>

                            <Link to={"dashboard"}>Dashboard</Link>
                        </Fragment>}
                    </div>
                </div>
            </Col>
            <Col xl={8} className="col">
                <div className="canvas">
                    <div className={"centered-container"}>
                        <span className="moving-circle third" data-name="Team 2"></span>
                        <span className="moving-circle first" data-name="Team 1"></span>
                        <span className="moving-circle second" data-name="Team 3"></span>
                    </div>
                </div>

                <h3 id={"about"}>What is Circle Work?</h3>
                <Card body>
                    CircleWork is a platform to push yourself to do better each day. Start your own circle or join
                    someone else's to start improving your productivity and get on top of your life!
                </Card>

                <h3 id={"donation"}>Our Charities</h3>
                <div className="charities">
                    {charities.map(i => //TODO
                        <a className="charity" href={i.link}>
                            <img src={`/images/charities/${i.image}`} className="logo" alt="Charity"/>
                            <span className="name">{i.name}</span>
                        </a>
                    )}
                </div>
            </Col>
            <Col xl={2} className="col">

            </Col>
        </Row>
    )
}
