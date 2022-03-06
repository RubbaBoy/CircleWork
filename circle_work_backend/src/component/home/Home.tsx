import React, { Fragment } from 'react'
import './Home.scss'
import {Card, Col, Row} from "react-bootstrap";
import {Link} from "react-router-dom";

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

    return (
        <Row className="Home justify-content-md-center m-0">
            <div className="side-buttons">
                {hrefSideButton('About', '#about')}
                {hrefSideButton('Donation Leaderboard', '#donation')}
            </div>

            <Col xl={2} className="left-col">
                <div className="sidebar">
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
                    Animation shit here
                </div>

                <h3 id={"about"}>What is Circle Work?</h3>
                <Card body>
                    CircleWork is a platform to push yourself to do better each day. Start your own circle or join
                    someone else's to start improving your productivity and get on top of your life!
                </Card>

                <h3 id={"donation"}>Our Charities</h3>
                <div className="charities">
                    {['one', 'two', 'three', 'four'].map(i => (
                        <div className="charity">
                            <img src="https://via.placeholder.com/150" alt="" className="logo"/>
                            <span className="name">Child's Play</span>
                        </div>
                    ))}
                </div>
            </Col>
            <Col xl={2} className="col">

            </Col>
        </Row>
    )
}
