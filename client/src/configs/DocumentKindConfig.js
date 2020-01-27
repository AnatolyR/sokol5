import axios from 'axios';

export function DocumentKindConfig() {
    return {
        maxItems: 1,
        //plugins: ['remove_button'],
        valueField: 'title',
        labelField: 'title',
        searchField: 'title',
        preload: true,
        // create: true,
        load(query, callback) {
            axios.get(`/api/documentKinds?size=100`).then((res) => {
                this.settings.load = null;
                const users = res.data._embedded.documentKinds;
                callback(users);
            }).catch(() => {
                callback();
            })
        }
    }
}