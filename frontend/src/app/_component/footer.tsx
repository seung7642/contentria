import Container from './container';

export function Footer() {
  return (
    <footer className="border-y bg-neutral-200">
      <Container>
        <div className="my-4 text-center">
          <p className="text-sm text-gray-500">© 2024</p>
        </div>
      </Container>
    </footer>
  );
}
