const express = require('express')
const router = express.Router()
const axios = require('axios');
const {post} = require("axios");
const REMOTE_API_BASE_URL = process.env.REMOTE_SERVER || 'http://localhost:8080';

router.post('/create', async (req, res) => {
    console.log(req.body)
    const token = req.headers['authorization']
    try {
        const result = await CreateInviteCode(req.body, token);
        res.status(200).json(result)
    } catch (error) {
        console.error('Error creating organization invite:', error.message);
        res.status(500).json({message: '创建组织邀请码失败', error: error.message, token: token});
    }
});

module.exports = router;

/*方法函数编写: */
/*创建组织邀请码*/
const CreateInviteCode = async (requstBody, token) => {
    const response = await axios.post(`${REMOTE_API_BASE_URL}/api/organization/invite/create`,
        requstBody, {
        headers: {
            'Authorization': token
        }
    })
    return response.data;
};