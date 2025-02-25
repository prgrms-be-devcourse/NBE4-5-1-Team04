// app/customer/[id]/update/page.tsx
import ClientCustomerUpdatePage from "./ClientCustomerUpdatePage";

export default async function Page({ params }: { params: { id: string } }) {
  return (
    <div>
      <ClientCustomerUpdatePage userId={params.id} />
    </div>
  );
}
