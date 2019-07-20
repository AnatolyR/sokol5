module.exports = {
    devServer: {
        port: 9090,
        proxy: {
            '/api/*': {
                target: 'http://localhost:8080',
            }
        },
        // proxy: 'http://localhost:8080'
    }
};