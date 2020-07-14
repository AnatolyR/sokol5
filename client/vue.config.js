module.exports = {
    devServer: {
        port: 9090,
        proxy: {
            '/api/*': {
                target: 'http://localhost:8080',
            }
        },
        watchOptions: {
            poll: 1000,
            ignored: /node_modules/,
        }
        // proxy: 'http://localhost:8080'
    }
};