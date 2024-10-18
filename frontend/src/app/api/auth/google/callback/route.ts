import { OAuth2Client } from 'google-auth-library';
import { NextRequest, NextResponse } from 'next/server';

const CLIENT_ID = '';
const CLIENT_SECRET = '';
const REDIRECT_URI = '';

const client = new OAuth2Client(CLIENT_ID, CLIENT_SECRET, REDIRECT_URI);

export async function GET(request: NextRequest) {
  const searchParams = request.nextUrl.searchParams;
  const code = searchParams.get('code');

  if (!code) {
    return NextResponse.json({ error: 'No code provided' }, { status: 400 });
  }

  try {
    const { tokens } = await client.getToken(code);
    client.setCredentials(tokens);

    const userInfo = await client.request({
      url: 'https://www.googleapis.com/oauth2/v3/userinfo',
    });

    // Here you would typically create a session or JWT for the user
    // For this example, we'll just return the user info
    return NextResponse.json(userInfo.data);
  } catch (error) {
    console.error('Error during token exchange:', error);
    return NextResponse.json({ error: 'Failed to authenticate' }, { status: 500 });
  }
}
