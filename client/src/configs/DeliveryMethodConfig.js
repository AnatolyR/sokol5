import axios from 'axios';

export function DeliveryMethodConfig() {
    return {
        maxItems: 1,
        //plugins: ['remove_button'],
        valueField: 'title',
        labelField: 'title',
        searchField: 'title',
        preload: true,
        // create: true,
        load(query, callback) {
            // if (!query.length) {
            //     return callback();
            // }
            // axios.get(`/api/deliveryMethods/search/methodByTitle?title=%25${query}%25`).then((res) => {
            axios.get(`/api/deliveryMethods?size=100`).then((res) => {
                this.settings.load = null;
                const users = res.data._embedded.deliveryMethods;
                callback(users);
            }).catch(() => {
                callback();
            })
        }
    }
}