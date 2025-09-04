interface AuthFormCardProps {
  title: string;
  children: React.ReactNode;
}

const AuthFormCard = ({ title, children }: AuthFormCardProps) => {
  return (
    <>
      <h2 className="text-center text-3xl font-extrabold text-gray-900">{title}</h2>
      <div className="relative mt-8 rounded-lg bg-white p-8 shadow-md">{children}</div>
    </>
  );
};

export default AuthFormCard;
