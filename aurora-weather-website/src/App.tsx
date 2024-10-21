import React from 'react';
import logo from './logo.svg';
import './App.css';
import {AuroraWeatherServiceClient} from "./proto/main_grpc_web_pb"
import {Empty} from "google-protobuf/google/protobuf/empty_pb";

function App() {

    call()

    return (
        <div className="App">
            <header className="App-header">
                <img src={logo} className="App-logo" alt="logo"/>
                <p>
                    Edit <code>src/App.tsx</code> and save to reload.
                </p>
                <a
                    className="App-link"
                    href="https://reactjs.org"
                    target="_blank"
                    rel="noopener noreferrer"
                >
                    Learn React
                </a>
            </header>
        </div>
    );
}

async function call() {
    const grpcHost = 'http://localhost:8080'; // Your gRPC web service endpoint

    const client = new AuroraWeatherServiceClient(grpcHost, null, null);
    client.getWeatherProviders(new Empty(), undefined, (err, response) => {
        if (err) {
            console.log(err.message);
        } else {
            console.log(response.getProvidersList())
        }
    })
}


export default App;
